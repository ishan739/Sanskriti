import express from "express";
import { getAllFestivals, getFestivalById, getFestivalsByRegion, getFestivalsByReligion, uploadFestivalImage } from "../controllers/Festival.controller.js";
import { uploadImage } from "../middlewares/multer.js";

const festivalrouter = express.Router();



festivalrouter.get("/",getAllFestivals);
festivalrouter.get("/:id", getFestivalById);
festivalrouter.get("/region/:region", getFestivalsByRegion);
festivalrouter.get("/religion/:religion", getFestivalsByReligion);

festivalrouter.post("/:id/upload-image",uploadImage.single('image'),uploadFestivalImage );

export default festivalrouter;
