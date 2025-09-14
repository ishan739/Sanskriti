import uploadOnCloudinary from "../config/cloudinary.js";
import Festival from "../models/Festival.js";



export const getAllFestivals= async(req,res)=>{
  try {
    const festivals = await Festival.find();
    res.status(200).json(festivals);
  } catch (error) {
    res.status(500).json({ message: "Error fetching foods", error });
  }
}

export const getFestivalById = async (req, res) => {
  try {
    const { id } = req.params;
    const festival = await Festival.findOne({ festivalId: id });

    if (!festival) {
      return res.status(404).json({ message: "Festival not found" });
    }

    res.json(festival);
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
};



export const getFestivalsByRegion = async (req, res) => {
  try {
    const { region } = req.params;
    const festivals = await Festival.find({ region });

    if (!festivals.length) {
      return res.status(404).json({ message: "No festivals found for this region" });
    }

    res.json(festivals);
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
};


export const getFestivalsByReligion = async (req, res) => {
  try {
    const { religion } = req.params;
    const festivals = await Festival.find({ religion });

    if (!festivals.length) {
      return res.status(404).json({ message: "No festivals found for this religion" });
    }

    res.json(festivals);
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
};


export const uploadFestivalImage = async (req, res) => {
    try {
        const { id } = req.params;
        if (!req.file) {
            return res.status(400).json({ message: 'No file uploaded' });
        }

        const imageUrl = await uploadOnCloudinary(req.file.path);

        const festival = await Festival.findOneAndUpdate(
            { festivalId: Number(id) },
            { image: imageUrl },
            { new: true }
        );

        if (!festival) {
            return res.status(404).json({ message: 'Festival not found' });
        }

        res.status(200).json({ message: 'Festival image uploaded successfully', festival });
    } catch (error) {
        res.status(500).json({ message: 'Server Error', error: error.message });
    }
};




export const updateFestival = async (req, res) => {
  try {
    const { id } = req.params;
    const updateData = req.body; 

    if (!updateData || Object.keys(updateData).length === 0) {
      return res.status(400).json({ message: "No update data provided" });
    }

    const updatedFestival = await Festival.findOneAndUpdate(
      { festivalId: Number(id) },
      { $set: updateData },
      { new: true, runValidators: true }
    );

    if (!updatedFestival) {
      return res.status(404).json({ message: "Festival not found" });
    }

    res.status(200).json({
      message: "Festival updated successfully",
      festival: updatedFestival,
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};
