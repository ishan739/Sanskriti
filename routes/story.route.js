import express from "express";


import { upload } from "../middlewares/multer.js";
import { getAllStories, getStoriesByCateogry, getStoryById, uploadStoryImage } from "../controllers/Story.controller.js";

const storyrouter= express.Router();

storyrouter.get("/",getAllStories);
storyrouter.get("/:id", getStoryById);
storyrouter.get("/category/:category", getStoriesByCateogry);

storyrouter.post('/:id/upload-image', upload.single('image'), uploadStoryImage);

export default storyrouter;
