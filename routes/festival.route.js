import express from "express";
import { getFestivalById, getFestivalsByRegion, getFestivalsByReligion, uploadFestivalImage } from "../controllers/Festival.controller.js";
import { upload } from "../middlewares/multer.js";

const festivalrouter = express.Router();

festivalrouter.get("/:id", getFestivalById);
festivalrouter.get("/region/:region", getFestivalsByRegion);
festivalrouter.get("/religion/:religion", getFestivalsByReligion);

festivalrouter.post("/:id/upload-image",upload.single('image'),uploadFestivalImage );

export default festivalrouter;
