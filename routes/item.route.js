import express from "express";
import { uploadImage } from "../middlewares/multer.js";
import { addItem, getAllItems, getItemById, getItemsByCategory, removeItem, searchItems, updateItem } from "../controllers/item.controller.js";
import { isAuth } from "../middlewares/authMiddleware.js";


const itemrouter = express.Router();

itemrouter.post("/add", uploadImage.single("image"), addItem);
itemrouter.delete("/:id", removeItem);
itemrouter.get("/", getAllItems);
itemrouter.get("/category/:category", getItemsByCategory);
itemrouter.get("/:id", getItemById);
itemrouter.put("/:id",  uploadImage.single("image"), updateItem);
itemrouter.get("/search/items", searchItems);


export default itemrouter;