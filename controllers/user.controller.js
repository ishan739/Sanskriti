import User from "../models/user.js";
import bcrypt from "bcryptjs";
import { OAuth2Client } from "google-auth-library";
import generateToken from "../config/generateToken.js";
import uploadOnCloudinary from "../config/cloudinary.js";
import transporter from "../config/mail.js";

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


export const sendOtp = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) return res.status(400).json({ message: "Email is required" });

    const user = await User.findOne({ email });
    if (!user) return res.status(404).json({ message: "User not found" });

    
    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    const otpExpiry = Date.now() + 10 * 60 * 1000; 

    user.resetPassOtp = otp;
    user.otpExpired = otpExpiry;
    user.isOtpVerified = false;
    await user.save();

    
    await transporter.sendMail({
      from: `"Sanskriti" <${process.env.EMAIL}>`,
      to: user.email,
      subject: "Password Reset OTP",
      html: `
  <!DOCTYPE html>
  <html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sanskriti - OTP Verification</title>
  </head>
  <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f9f5f0;">
    <table width="100%" cellpadding="0" cellspacing="0" style="max-width:600px; margin:20px auto; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.1);">
      <tr>
        <td style="background:#8B0000; padding:20px; text-align:center; color:#fff;">
          <h1 style="margin:0; font-size:26px; letter-spacing:1px;">Sanskriti</h1>
          <p style="margin:0; font-size:14px;">Celebrating India’s Rich Heritage & Culture</p>
        </td>
      </tr>
      <tr>
        <td style="padding:30px; color:#333; text-align:center;">
          <h2 style="margin:0 0 15px; font-size:22px; color:#8B0000;">Your OTP Verification Code</h2>
          <p style="font-size:16px; line-height:1.6; margin:0 0 20px;">
            Dear <strong>${user.name || "User"}</strong>,<br><br>
            Use the OTP below to reset your password. This OTP is valid for <b>10 minutes</b>.
          </p>
          <div style="font-size:28px; font-weight:bold; letter-spacing:6px; color:#8B0000; margin:20px 0;">
            ${otp}
          </div>
          <p style="font-size:14px; color:#666; margin:20px 0;">
            If you didn’t request this, please ignore this email.
          </p>
        </td>
      </tr>
      <tr>
        <td style="background:#f2e6d9; padding:15px; text-align:center; font-size:12px; color:#555;">
          &copy; ${new Date().getFullYear()} Sanskriti. Preserving India’s heritage digitally.<br>
          <a href="#" style="color:#8B0000; text-decoration:none;">Visit Our Website</a>
        </td>
      </tr>
    </table>
  </body>
  </html>
  `
    });

    res.status(200).json({ message: "OTP sent successfully to email" });
  } catch (error) {
    res.status(500).json({ message: "Error sending OTP", error: error.message });
  }
};


export const verifyOtp = async (req, res) => {
  try {
    const { email, otp } = req.body;

    if (!email || !otp)
      return res.status(400).json({ message: "Email and OTP are required" });

    const user = await User.findOne({ email });
    if (!user) return res.status(404).json({ message: "User not found" });

    if (user.resetPassOtp !== otp)
      return res.status(400).json({ message: "Invalid OTP" });

    if (user.otpExpired < Date.now())
      return res.status(400).json({ message: "OTP expired" });

    user.isOtpVerified = true;
    await user.save();

    res.status(200).json({ message: "OTP verified successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error verifying OTP", error: error.message });
  }
};


export const resetPassword = async (req, res) => {
  try {
    const { email, newPassword } = req.body;

    if (!email || !newPassword)
      return res.status(400).json({ message: "Email and new password required" });

    const user = await User.findOne({ email });
    if (!user) return res.status(404).json({ message: "User not found" });

    if (!user.isOtpVerified)
      return res
        .status(400)
        .json({ message: "OTP not verified. Please verify OTP first." });

    const hashedPassword = await bcrypt.hash(newPassword, 10);

    user.password = hashedPassword;
    user.resetPassOtp = null;
    user.otpExpired = null;
    user.isOtpVerified = false;
    await user.save();

    res.status(200).json({ message: "Password reset successful" });
  } catch (error) {
    res.status(500).json({ message: "Error resetting password", error: error.message });
  }
};



