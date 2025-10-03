import Cart from "../models/cart.js";
import Item from "../models/item.js";


export const addItemToCart = async (req, res) => {
  try {
    const userId = req.userId; 
    const { itemId, quantity } = req.body;

    if (!itemId || !quantity || quantity < 1) {
      return res.status(400).json({ message: "Item ID and valid quantity are required" });
    }

   
    const item = await Item.findById(itemId);
    if (!item) {
      return res.status(404).json({ message: "Item not found" });
    }

  
    let cart = await Cart.findOne({ user: userId });
    if (!cart) {
      cart = new Cart({ user: userId, items: [], totalAmount: 0 });
    }

    const existingItem = cart.items.find(i => i.item.toString() === itemId);

    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      cart.items.push({
        item: item._id,
        quantity,
        priceAtPurchase: item.price,
      });
    }

    cart.totalAmount = cart.items.reduce((sum, i) => sum + i.quantity * i.priceAtPurchase, 0);

    await cart.save();
    return res.status(200).json(cart);
  } catch (error) {
    console.error("addItemToCart error:", error);
    return res.status(500).json({ message: `addItemToCart error ${error.message}` });
  }
};



export const getCart = async (req, res) => {
  try {
    const userId = req.userId;

    const cart = await Cart.findOne({ user: userId }).populate("items.item");
    if (!cart) {
      return res.status(200).json({ items: [], totalAmount: 0 });
    }

    return res.status(200).json(cart);
  } catch (error) {
    console.error("getCart error:", error);
    return res.status(500).json({ message: `getCart error ${error.message}` });
  }
};



export const removeItemFromCart = async (req, res) => {
  try {
    const userId = req.userId;
    const { itemId } = req.params;

    let cart = await Cart.findOne({ user: userId });
    if (!cart) {
      return res.status(404).json({ message: "Cart not found" });
    }

    cart.items = cart.items.filter(i => i.item.toString() !== itemId);

    
    cart.totalAmount = cart.items.reduce((sum, i) => sum + i.quantity * i.priceAtPurchase, 0);

    await cart.save();
    return res.status(200).json(cart);
  } catch (error) {
    console.error("removeItemFromCart error:", error);
    return res.status(500).json({ message: `removeItemFromCart error ${error.message}` });
  }
};



export const updateCartItemQuantity = async (req, res) => {
  try {
    const userId = req.userId;
    const { itemId } = req.params;
    const { quantity } = req.body;

    if (quantity < 1) {
      return res.status(400).json({ message: "Quantity must be at least 1" });
    }

    let cart = await Cart.findOne({ user: userId });
    if (!cart) {
      return res.status(404).json({ message: "Cart not found" });
    }

    const cartItem = cart.items.find(i => i.item.toString() === itemId);
    if (!cartItem) {
      return res.status(404).json({ message: "Item not in cart" });
    }

    cartItem.quantity = quantity;

    cart.totalAmount = cart.items.reduce((sum, i) => sum + i.quantity * i.priceAtPurchase, 0);

    await cart.save();
    return res.status(200).json(cart);
  } catch (error) {
    console.error("updateCartItemQuantity error:", error);
    return res.status(500).json({ message: `updateCartItemQuantity error ${error.message}` });
  }
};
