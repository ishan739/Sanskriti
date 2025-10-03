import express from "express";
import { getAllMusic, getMusicById, getMusicsByType, uploadMusicImage } from "../controllers/Music.controller.js";
import { uploadImage } from "../middlewares/multer.js";


const musicrouter = express.Router();

musicrouter.get("/", getAllMusic);
musicrouter.get("/:id", getMusicById);
musicrouter.get("/type/:type", getMusicsByType);


musicrouter.post('/:id/upload-image', uploadImage.single('image'), uploadMusicImage);

export default musicrouter;