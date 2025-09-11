import express from "express";
import { getAllArts, getArtById, getArtsByType, uploadArtImage } from "../controllers/Art.controller.js";
import { uploadImage } from "../middlewares/multer.js";

const artrouter = express.Router();

artrouter.get("/", getAllArts);
artrouter.get("/:id", getArtById);
artrouter.get("/type/:type", getArtsByType);

artrouter.post('/:id/upload-image', uploadImage.single('image'), uploadArtImage);

export default artrouter;