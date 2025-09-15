import axios from "axios";
import FormData from "form-data";
import fs from "fs";
import path from "path";

const FASTAPI_URL = "https://image-2-text.onrender.com/predict";

export const detectMonument = async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ error: "No image uploaded" });
        }

        // File path (disk storage)
        const filePath = path.join(process.cwd(), req.file.path);

        const formData = new FormData();
        formData.append("file", fs.createReadStream(filePath));

        const fastapiRes = await axios.post(FASTAPI_URL, formData, {
            headers: formData.getHeaders(),
            maxBodyLength: Infinity,
            maxContentLength: Infinity,
            timeout: 120000, // 2 min timeout
        });

        const placeName = fastapiRes.data.monument; // ✅ using FastAPI key

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

        res.json({
            name: placeName,
            officialTitle: wikiData.title || "",
            tagline: wikiData.description || "", // short tagline
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
        res
            .status(500)
            .json({ error: "Something went wrong", details: error.message });
    }
};
