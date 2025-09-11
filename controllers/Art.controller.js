import uploadOnCloudinary from "../config/cloudinary.js";
import Art from "../models/art.js";

export const getAllArts = async (req, res) => {
  try {
    const arts = await Art.find();
    res.status(200).json(arts);
  } catch (error) {
    res.status(500).json({ message: "Error fetching arts", error: error.message });
  }
};


export const getArtById = async (req, res) => {
  try {
    const art = await Art.findOne({ id: req.params.id });

    if (!art) {
      return res.status(404).json({ message: "Art not found" });
    }

    res.status(200).json(art);
  } catch (error) {
    res.status(500).json({ message: "Error fetching art", error: error.message });
  }
};


export const getArtsByType = async (req, res) => {
  try {
    const { type } = req.params;
    const arts = await Art.find({ type });

    if (!arts.length) {
      return res.status(404).json({ message: "No arts found for this type" });
    }

    res.status(200).json(arts);
  } catch (error) {
    res.status(500).json({ message: "Error fetching arts by type", error: error.message });
  }
};

export const uploadArtImage = async (req, res) => {
  try {
    const { id } = req.params;

    if (!req.file) {
      return res.status(400).json({ message: "No file uploaded" });
    }

    const imageUrl = await uploadOnCloudinary(req.file.path);

    const updatedArt = await Art.findOneAndUpdate(
      { id: Number(id) },
      { imageurl: imageUrl },
      { new: true }
    );

    if (!updatedArt) {
      return res.status(404).json({ message: "Art not found" });
    }

    res.status(200).json({
      message: "Art image uploaded successfully",
      art: updatedArt,
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};
