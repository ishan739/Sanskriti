import express from "express";
import { loginValidation, signupValidation } from "../validation/userValidation.js";
import { validateRequest } from "../middlewares/validateRequest.js";
import { googleLogin, login, signup } from "../controllers/user.controller.js";

const userrouter=express.Router();

userrouter.post("/signup", validateRequest(signupValidation), signup);
userrouter.post("/login", validateRequest(loginValidation), login);
userrouter.post("/google", googleLogin);

export default userrouter;