
import uploadOnCloudinary from "../config/cloudinary.js";
import food from "../models/food.js";




export const getAllFoods = async (req, res) => {
  try {
    const foods = await food.find();
    res.status(200).json(foods);
  } catch (error) {
    res.status(500).json({ message: "Error fetching foods", error });
  }
};


export const getFoodById = async (req, res) => {
  try {
    const { id } = req.params;
    const foodItem = await food.findOne({ id: Number(id) }); 

    if (!foodItem) {
      return res.status(404).json({ message: "Food not found" });
    }

    res.status(200).json(foodItem);
  } catch (error) {
    res.status(500).json({ message: "Error fetching food by ID", error });
  }
};



export const getFoodsByType = async (req, res) => {
  try {
    const { type } = req.params;
    const foods = await food.find({ type });;

    if (!foods.length) {
      return res.status(404).json({ message: "No foods found for this type" });
    }

    res.status(200).json(foods);
  } catch (error) {
    res.status(500).json({ message: "Error fetching foods by type", error });
  }
};


export const getFoodsByRegion = async (req, res) => {
  try {
    const { region } = req.params;
     const foods = await food.find({ region });

    if (!foods.length) {
      return res.status(404).json({ message: "No foods found for this region" });
    }

    res.status(200).json(foods);
  } catch (error) {
    res.status(500).json({ message: "Error fetching foods by region", error });
  }
};


export const uploadFoodImage = async (req, res) => {
  try {
    const { id } = req.params;

    if (!req.file) {
      return res.status(400).json({ message: "No file uploaded" });
    }

    const imageUrl = await uploadOnCloudinary(req.file.path);

    const updatedFood = await food.findOneAndUpdate(
      { id: Number(id) },
      { imageurl: imageUrl },
      { new: true }
    );

    if (!updatedFood) {
      return res.status(404).json({ message: "Food not found" });
    }

    res.status(200).json({
      message: "Food image uploaded successfully",
      food: updatedFood,
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};


export const updateFood = async (req, res) => {
  try {
    const { id } = req.params;
    const updateData = req.body; 

    if (!updateData || Object.keys(updateData).length === 0) {
      return res.status(400).json({ message: "No update data provided" });
    }

    const updatedFood = await food.findOneAndUpdate(
      { id: Number(id) },
      { $set: updateData },
      { new: true, runValidators: true }
    );

    if (!updatedFood) {
      return res.status(404).json({ message: "Food not found" });
    }

    res.status(200).json({
      message: "Food updated successfully",
      food: updatedFood,
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};
