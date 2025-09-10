import express from "express";

import { uploadImage } from "../middlewares/multer.js";
import { getAllDances, getDanceById, getDancesByType } from "../controllers/Dance.controller.js";

const dancerouter = express.Router();

dancerouter.get("/", getAllDances);
dancerouter.get("/:id", getDanceById);
dancerouter.get("/type/:type", getDancesByType);

export default dancerouter;