
import Dance from "../models/dance.js";

export const getAllDances = async (req, res) => {
  try {
    const dances = await Dance.find();
    res.status(200).json(dances);
  } catch (error) {
    res.status(500).json({ message: "Error fetching dances", error: error.message });
  }
};


export const getDanceById = async (req, res) => {
  try {
    const { id } = req.params;
    const dance = await Dance.findOne({ id: Number(id) });

    if (!dance) {
      return res.status(404).json({ message: "Dance not found" });
    }

    res.status(200).json(dance);
  } catch (error) {
    res.status(500).json({ message: "Error fetching dance", error: error.message });
  }
};


export const getDancesByType = async (req, res) => {
  try {
    const { type } = req.params;
    const dances = await Dance.find({ type: new RegExp(type, "i") }); 

    if (!dances.length) {
      return res.status(404).json({ message: "No dances found for this type" });
    }

    res.status(200).json(dances);
  } catch (error) {
    res.status(500).json({ message: "Error fetching dances by type", error: error.message });
  }
};



