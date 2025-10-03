import uploadOnCloudinary from "../config/cloudinary.js";
import Item from "../models/item.js";



export const addItem = async (req, res) => {
  try {
    const { name, category, description, price, stock, materialUsed, origin, isTraditional } = req.body;

    if (!name || !category || !price) {
      return res.status(400).json({ message: "Name, category, and price are required" });
    }

    let imageUrl = "";
    if (req.file) {
  const uploadedImage = await uploadOnCloudinary(req.file.path);
  imageUrl = uploadedImage || "";
}

    const newItem = await Item.create({
      name,
      category,
      description,
      price,
      stock,
      imageUrl,
      materialUsed,
      origin,
      isTraditional,
    });

    return res.status(201).json(newItem);
  } catch (error) {
    console.error("addItem error:", error);
    return res.status(500).json({ message: `addItem error ${error.message}` });
  }
};


export const removeItem = async (req, res) => {
  try {
    const { id } = req.params;
    const item = await Item.findByIdAndDelete(id);

    if (!item) {
      return res.status(404).json({ message: "Item not found" });
    }

    return res.status(200).json({ message: "Item deleted successfully" });
  } catch (error) {
    console.error("removeItem error:", error);
    return res.status(500).json({ message: `removeItem error ${error.message}` });
  }
};


export const getAllItems = async (req, res) => {
  try {
    const items = await Item.find().sort({ createdAt: -1 });
    return res.status(200).json(items);
  } catch (error) {
    console.error("getAllItems error:", error);
    return res.status(500).json({ message: `getAllItems error ${error.message}` });
  }
};


export const getItemsByCategory = async (req, res) => {
  try {
    const { category } = req.params;
    const items = await Item.find({ category });

    if (!items.length) {
      return res.status(404).json({ message: "No items found in this category" });
    }

    return res.status(200).json(items);
  } catch (error) {
    console.error("getItemsByCategory error:", error);
    return res.status(500).json({ message: `getItemsByCategory error ${error.message}` });
  }
};


export const getItemById = async (req, res) => {
  try {
    const { id } = req.params;
    const item = await Item.findById(id);

    if (!item) {
      return res.status(404).json({ message: "Item not found" });
    }

    return res.status(200).json(item);
  } catch (error) {
    console.error("getItemById error:", error);
    return res.status(500).json({ message: `getItemById error ${error.message}` });
  }
};


export const updateItem = async (req, res) => {
  try {
    const { id } = req.params;
    const updates = req.body;

    if (req.file) {
  const uploadedImage = await uploadOnCloudinary(req.file.path);
  updates.imageUrl = uploadedImage || "";
}


    const updatedItem = await Item.findByIdAndUpdate(id, updates, { new: true });

    if (!updatedItem) {
      return res.status(404).json({ message: "Item not found" });
    }

    return res.status(200).json(updatedItem);
  } catch (error) {
    console.error("updateItem error:", error);
    return res.status(500).json({ message: `updateItem error ${error.message}` });
  }
};


export const searchItems = async (req, res) => {
  try {
    const { query } = req.query;
    const items = await Item.find({
      $or: [
        { name: { $regex: query, $options: "i" } },
        { description: { $regex: query, $options: "i" } },
      ],
    });

    return res.status(200).json(items);
  } catch (error) {
    console.error("searchItems error:", error);
    return res.status(500).json({ message: `searchItems error ${error.message}` });
  }
};
