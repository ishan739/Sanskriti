import express from "express";

import { uploadImage } from "../middlewares/multer.js";
import { getAllFoods, getFoodById, getFoodsByRegion, getFoodsByType, uploadFoodImage } from "../controllers/Food.controller.js";
import food from "../models/food.js";


const foodrouter = express.Router();

foodrouter.get("/", getAllFoods);
foodrouter.get("/:id", getFoodById);
foodrouter.get("/type/:type", getFoodsByType);
foodrouter.get("/region/:region", getFoodsByRegion);

foodrouter.post('/:id/upload-image', uploadImage.single('image'), uploadFoodImage);

export default foodrouter;
