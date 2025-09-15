import axios from "axios";
import FormData from "form-data";
import fs from "fs";
import path from "path";

const FASTAPI_URL = "https://image-2-text.onrender.com/predict";

export const detectMonument = async (req, res) => {
  let filePath; // keep reference for cleanup

  try {
    if (!req.file) {
      return res.status(400).json({ error: "No image uploaded" });
    }

    // File path (disk storage)
    filePath = path.join(process.cwd(), req.file.path);

    const formData = new FormData();
    formData.append("file", fs.createReadStream(filePath));

    const fastapiRes = await axios.post(FASTAPI_URL, formData, {
      headers: formData.getHeaders(),
      maxBodyLength: Infinity,
      maxContentLength: Infinity,
      timeout: 120000, // 2 min timeout
    });

    const placeName = fastapiRes.data.monument; // ✅ FastAPI key

    if (!placeName) {
      return res.status(404).json({ error: "No monument detected" });
    }

    // Fetch from Wikipedia
    const wikiRes = await axios.get(
      `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(
        placeName
      )}`
    );

    const wikiData = wikiRes.data;

    // ✅ Delete uploaded file after processing
    fs.unlink(filePath, (err) => {
      if (err) console.error("⚠️ Error deleting file:", err.message);
      else console.log("🗑️ Deleted file:", filePath);
    });

    res.json({
      name: placeName,
      officialTitle: wikiData.title || "",
      tagline: wikiData.description || "",
      description: wikiData.extract || "No description available",
      extractHtml: wikiData.extract_html || "",
      image: wikiData.thumbnail?.source || "",
      originalImage: wikiData.originalimage?.source || "",
      wikiUrl: wikiData.content_urls?.desktop?.page || "",
      mobileWikiUrl: wikiData.content_urls?.mobile?.page || "",
    });
  } catch (error) {
    console.error(
      "❌ Monument detection error:",
      error.response?.data || error.message
    );

    
    if (filePath) {
      fs.unlink(filePath, (err) => {
        if (err) console.error("⚠️ Error deleting file after failure:", err.message);
      });
    }

    res.status(500).json({ error: "Something went wrong", details: error.message });
  }
};
