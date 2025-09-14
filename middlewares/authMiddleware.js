// middlewares/authMiddleware.js
import jwt from "jsonwebtoken";
import User from "../models/user.js";

export const isAuth = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    console.log("Authorization header:", authHeader);

    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      return res.status(401).json({ message: "Not authorized, no token" });
    }

    const token = authHeader.split(" ")[1];
    console.log("Extracted token:", token);

    if (!token) {
      return res.status(401).json({ message: "Not authorized, no token" });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const user = await User.findById(decoded.id).select("-password");

    if (!user) {
      return res.status(401).json({ message: "Not authorized, user not found" });
    }

    req.user = user;
    req.userId = user._id.toString();

    return next();
  } catch (error) {
    console.error("is auth error", error);
    return res.status(401).json({ message: "Not authorized, invalid token" });
  }
};
