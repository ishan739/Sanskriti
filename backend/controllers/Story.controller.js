import uploadOnCloudinary from "../config/cloudinary.js";
import story from "../models/story.js";



export const getAllStories = async (req, res) => {
  try {
    const stories = await story.find();
    res.status(200).json(stories);
  } catch (error) {
    res.status(500).json({ message: "Error fetching story", error });
  }
};


export const getStoryById = async (req, res) => {
  try {
    const { id } = req.params;
    const storyitem = await story.findOne({ id: Number(id) }); 

    if (!storyitem) {
      return res.status(404).json({ message: "Story not found" });
    }

    res.status(200).json(storyitem);
  } catch (error) {
    res.status(500).json({ message: "Error fetching story by ID", error });
  }
};


export const getStoriesByCateogry = async (req, res) => {
  try {
    const { category } = req.params;
     const stories = await story.find({ category });

    if (!stories.length) {
      return res.status(404).json({ message: "No stories found for this category" });
    }

    res.status(200).json(stories);
  } catch (error) {
    res.status(500).json({ message: "Error fetching stories by cateogry", error });
  }
};


export const uploadStoryImage = async (req, res) => {
  try {
    const { id } = req.params;

    if (!req.file) {
      return res.status(400).json({ message: "No file uploaded" });
    }

    const imageUrl = await uploadOnCloudinary(req.file.path);

    const updatedStory = await story.findOneAndUpdate(
      { id: Number(id) },
      { thumbnail: imageUrl },
      { new: true }
    );

    if (!updatedStory) {
      return res.status(404).json({ message: "Story not found" });
    }

    res.status(200).json({
      message: "Story image uploaded successfully",
      story: updatedStory,
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};



export const uploadStoryAudio = async (req, res) => {
  try {
    const { id } = req.params;

    if (!req.file) {
      return res.status(400).json({ message: "No audio file uploaded" });
    }

    
    const audioUrl = await uploadOnCloudinary(req.file.path);

    
    const updatedStory = await story.findOneAndUpdate(
      { id: Number(id) },
      { audiourl: audioUrl },
      { new: true }
    );

    if (!updatedStory) {
      return res.status(404).json({ message: "Story not found" });
    }

    res.status(200).json({
      message: "Audio uploaded successfully",
      story: updatedStory,
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

