import User from "../models/user.js";
import bcrypt from "bcryptjs";
import { OAuth2Client } from "google-auth-library";
import generateToken from "../config/generateToken.js";
import uploadOnCloudinary from "../config/cloudinary.js";

const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);


export const signup = async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({ message: "All fields are required" });
    }

    const existingUser = await User.findOne({email});
    if (existingUser) {
      return res.status(400).json({ message: "User already exists" });
    }

    const hashedPassword = await bcrypt.hash(password, 12);

    const user = await User.create({
      name,
      email,
      password: hashedPassword,
    });

    res.status(201).json({
      message: "User registered successfully",
      token: generateToken(user._id),
      user,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};


export const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });
    if (!user) return res.status(400).json({ message: "Invalid credentials" });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch)
      return res.status(400).json({ message: "Invalid credentials" });

    res.status(200).json({
      message: "Login successful",
      token: generateToken(user._id),
      user,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};


export const googleLogin = async (req, res) => {
  try {
    const { tokenId } = req.body;
    const ticket = await client.verifyIdToken({
      idToken: tokenId,
      audience: process.env.GOOGLE_CLIENT_ID,
    });

    const { name, email, picture } = ticket.getPayload();

    let user = await User.findOne({ email });
    if (!user) {
      user = await User.create({
        name,
        email,
        username: email.split("@")[0],
        password: await bcrypt.hash(Math.random().toString(36).slice(-8), 12),
        profileImage: picture,
      });
    }

    res.status(200).json({
      message: "Google login successful",
      token: generateToken(user._id),
      user,
    });
  } catch (error) {
    res.status(500).json({ message: "Google login failed" });
  }
};


export const getProfile = async (req, res) => {
  try {
    const userId = req.userId; 
    if (!userId) return res.status(401).json({ message: "Not authorized" });

    const user = await User.findById(userId)
      .select("-password -resetPassOtp -otpExpired"); 
    if (!user) return res.status(404).json({ message: "User not found" });

    res.status(200).json(user);
  } catch (error) {
    res.status(500).json({ message: `getProfile error: ${error.message}` });
  }
};



export const editProfile = async (req, res) => {
  try {
    const userId = req.userId;
    if (!userId) return res.status(401).json({ message: "Not authorized" });

    const { name, bio, gender } = req.body;

   
    const updateData = {};
    if (name) updateData.name = name;
    if (bio) updateData.bio = bio;
    if (gender) updateData.gender = gender;

    if (req.file) {
      const cloudImage = await uploadOnCloudinary(req.file.path);
      updateData.profileImage = cloudImage;
    }

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      { $set: updateData },
      { new: true }
    ).select("-password -resetPassOtp -otpExpired");

    if (!updatedUser) return res.status(404).json({ message: "User not found" });

    res.status(200).json({
      message: "Profile updated successfully",
      user: updatedUser,
    });
  } catch (error) {
    res.status(500).json({ message: `editProfile error: ${error.message}` });
  }
};




