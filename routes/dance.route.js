import express from "express";

import { uploadImage } from "../middlewares/multer.js";
import { getAllDances, getDanceById, getDancesByType, uploadDanceImage } from "../controllers/Dance.controller.js";

const dancerouter = express.Router();

dancerouter.get("/", getAllDances);
dancerouter.get("/:id", getDanceById);
dancerouter.get("/type/:type", getDancesByType);

dancerouter.post('/:id/upload-image', uploadImage.single('image'), uploadDanceImage);

export default dancerouter;