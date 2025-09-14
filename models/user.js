import mongoose from "mongoose";


const postRef = {
  type: mongoose.Schema.Types.ObjectId,
  ref: "Post",
};


const cartRef = {
  type: mongoose.Schema.Types.ObjectId,
  ref: "Cart",
};


const monumentRef = {
  type: mongoose.Schema.Types.ObjectId,
  ref: "Monument",
};

const userSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: true,
      trim: true,
    },
    email: {
      type: String,
      required: true,
      unique: true,
      lowercase: true,
      trim: true,
    },
    password: {
      type: String,
      required: true,
    },
    profileImage: {
      type: String,
      default: "",
    },
    bio: {
      type: String,
      trim: true,
    },
    gender: {
      type: String,
      enum: ["Male", "Female", "Other"],
    },

    
    posts: [postRef],
    cart: [cartRef],
    savedPlaces: [monumentRef],

    
    resetPassOtp: {
      type: String,
    },
    otpExpired: {
      type: Date,
    },
    isOtpVerified: {
      type: Boolean,
      default: false,
    },
  },
  { timestamps: true }
);

const User = mongoose.model("User", userSchema);

export default User;
