import express from "express";
import { uploadImage } from "../middlewares/multer.js";
import { detectMonument } from "../controllers/predict.js";


const predictrouter = express.Router();

predictrouter.post("/up", uploadImage.single("image"), detectMonument);

export default predictrouter