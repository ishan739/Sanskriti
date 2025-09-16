import express from "express";
import { eventFinder } from "../controllers/event.controller.js";

const eventrouter = express.Router();



eventrouter.post("/",eventFinder);

export default eventrouter;