import express from "express";
import { getEvents } from "../controllers/event.controller.js";


const eventrouter = express.Router();

eventrouter.post("/",getEvents);


export default eventrouter;