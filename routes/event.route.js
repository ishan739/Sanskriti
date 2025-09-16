import express from "express";
import getEventsController from "../controllers/event.controller.js";


const eventrouter = express.Router();


eventrouter.post('/', getEventsController);


export default eventrouter;