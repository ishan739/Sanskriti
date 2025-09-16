import express from "express";
import { isAuth } from "../middlewares/authMiddleware..js";
import { addItemToCart, getCart, removeItemFromCart, updateCartItemQuantity } from "../controllers/cart.controller.js";



const cartrouter = express.Router();

cartrouter.get("/", isAuth, getCart);
cartrouter.post("/add", isAuth, addItemToCart);
cartrouter.put("/update/:itemId", isAuth, updateCartItemQuantity);
cartrouter.delete("/remove/:itemId", isAuth, removeItemFromCart);

export default cartrouter;
