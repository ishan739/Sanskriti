import express from "express";
import { getAllArts, getArtById, getArtsByType, uploadArtImage, uploadArtVideo } from "../controllers/Art.controller.js";
import { uploadImage, uploadVideo } from "../middlewares/multer.js";

const artrouter = express.Router();

artrouter.get("/", getAllArts);
artrouter.get("/:id", getArtById);
artrouter.get("/type/:type", getArtsByType);

artrouter.post('/:id/upload-image', uploadImage.single('image'), uploadArtImage);
artrouter.post('/:id/upload-video', uploadVideo.single('video'), uploadArtVideo);

export default artrouter;