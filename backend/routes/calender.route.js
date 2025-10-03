
import express from "express";
import { getFestivalsByMonth, getFestivalsByYear, getUpcomingFestivals } from "../controllers/calender.js";


const calenderrouter = express.Router();

calenderrouter.get("/year/:year", getFestivalsByYear);
calenderrouter.get("/month/:year/:month", getFestivalsByMonth);
calenderrouter.get("/upcoming", getUpcomingFestivals);

export default calenderrouter;
