import express from "express";
import { loginValidation, signupValidation } from "../validation/userValidation.js";
import { validateRequest } from "../middlewares/validateRequest.js";
import { editProfile, getProfile, googleLogin, login, signup } from "../controllers/user.controller.js";
import { isAuth } from "../middlewares/authMiddleware.js";
import { uploadImage } from "../middlewares/multer.js";

const userrouter=express.Router();

userrouter.post("/signup", validateRequest(signupValidation), signup);
userrouter.post("/login", validateRequest(loginValidation), login);
userrouter.post("/google", googleLogin);

userrouter.get("/profile", isAuth, getProfile);
userrouter.put(
  "/profile",
  isAuth,
  uploadImage.single("profileImage"), 
  editProfile
);

export default userrouter;