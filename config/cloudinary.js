import { v2 as cloudinary } from "cloudinary";
import fs from "fs";

const uploadOnCloudinary = async (file) => {
  try {
    cloudinary.config({
      cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
      api_key: process.env.CLOUDINARY_API_KEY,
      api_secret: process.env.CLOUDINARY_API_SECRET,
    });

    // Detect file extension
    const ext = file.split(".").pop().toLowerCase();

    let resourceType = "auto";
    if (["mp3", "wav", "m4a"].includes(ext)) {
      resourceType = "video"; // Cloudinary uses "video" pipeline for audio files
    }

    const result = await cloudinary.uploader.upload(file, {
      resource_type: resourceType,
    });

    // Remove local file after upload
    fs.unlinkSync(file);

    return result.secure_url;
  } catch (error) {
    if (fs.existsSync(file)) {
      fs.unlinkSync(file);
    }
    console.error("Cloudinary upload error:", error);
    throw error;
  }
};

export default uploadOnCloudinary;
