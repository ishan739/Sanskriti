import uploadOnCloudinary from "../config/cloudinary.js";
import Music from "../models/music.js";

export const getAllMusic = async (req, res) => {
  try {
    const musics = await Music.find();
    res.status(200).json(musics);
  } catch (error) {
    res.status(500).json({ message: "Error fetching music", error: error.message });
  }
};


export const getMusicById = async (req, res) => {
  try {
    const music = await Music.findOne({ id: req.params.id });

    if (!music) {
      return res.status(404).json({ message: "Music not found" });
    }

    res.status(200).json(music);
  } catch (error) {
    res.status(500).json({ message: "Error fetching music", error: error.message });
  }
};


export const getMusicsByType = async (req, res) => {
  try {
    const { type } = req.params;
    const musics = await Music.find({ type });

    if (!musics.length) {
      return res.status(404).json({ message: "No music found for this type" });
    }

    res.status(200).json(musics);
  } catch (error) {
    res.status(500).json({ message: "Error fetching musics by type", error: error.message });
  }
};

export const uploadMusicImage = async (req, res) => {
  try {
    const { id } = req.params;

    if (!req.file) {
      return res.status(400).json({ message: "No file uploaded" });
    }

    const imageUrl = await uploadOnCloudinary(req.file.path);

    const updatedMusic = await Music.findOneAndUpdate(
      { id: Number(id) },
      { imageurl: imageUrl },
      { new: true }
    );

    if (!updatedMusic) {
      return res.status(404).json({ message: "Music not found" });
    }

    res.status(200).json({
      message: "Music image uploaded successfully",
      music: updatedMusic,
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};
