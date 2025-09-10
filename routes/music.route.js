import express from "express";
import { getAllMusic, getMusicById, getMusicsByType } from "../controllers/Music.controller.js";


const musicrouter = express.Router();

musicrouter.get("/", getAllMusic);
musicrouter.get("/:id", getMusicById);
musicrouter.get("/type/:type", getMusicsByType);

export default musicrouter;