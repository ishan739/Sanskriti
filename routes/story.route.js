import express from "express";


import { uploadImage, uploadAudio } from "../middlewares/multer.js";
import { getAllStories, getStoriesByCateogry, getStoryById, uploadStoryAudio, uploadStoryImage } from "../controllers/Story.controller.js";

const storyrouter= express.Router();

storyrouter.get("/",getAllStories);
storyrouter.get("/:id", getStoryById);
storyrouter.get("/category/:category", getStoriesByCateogry);

storyrouter.post('/:id/upload-image', uploadImage.single('image'), uploadStoryImage);
storyrouter.post('/:id/upload-audio', uploadAudio.single('audio'),uploadStoryAudio);

export default storyrouter;
