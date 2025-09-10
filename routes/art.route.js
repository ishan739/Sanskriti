import express from "express";
import { getAllArts, getArtById, getArtsByType } from "../controllers/Art.controller.js";

const artrouter = express.Router();

artrouter.get("/", getAllArts);
artrouter.get("/:id", getArtById);
artrouter.get("/type/:type", getArtsByType);

export default artrouter;