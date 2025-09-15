import axios from "axios";
import FormData from "form-data";
import fs from "fs";
import path from "path";

const FASTAPI_URL = "https://image-2-text.onrender.com/predict";

export const detectMonument = async (req, res) => {
    let filePath;

    try {
        if (!req.file) {
            return res.status(400).json({ error: "No image uploaded" });
        }

        // Save path of uploaded file
        filePath = path.join(process.cwd(), req.file.path);

        // Send file to FastAPI
        const formData = new FormData();
        formData.append("file", fs.createReadStream(filePath));

        const fastapiRes = await axios.post(FASTAPI_URL, formData, {
            headers: {
                ...formData.getHeaders(),
                "Cache-Control": "no-cache",
                Pragma: "no-cache",
                Expires: "0",
            },
            maxBodyLength: Infinity,
            maxContentLength: Infinity,
            timeout: 120000,
        });

        console.log("⚡ FastAPI Response:", fastapiRes.data);

        const placeName = fastapiRes.data.monument;
        if (!placeName) {
            return res.status(404).json({ error: "No monument detected" });
        }

        // Normalize monument name for Wikipedia
        const normalizedName = placeName.trim().replace(/\s+/g, "_");

        // Fetch summary from Wikipedia
        let wikiRes = await axios.get(
            `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(normalizedName)}`,
            {
                headers: {
                    "Cache-Control": "no-cache",
                    Pragma: "no-cache",
                    Expires: "0",
                },
            }
        );

        let wikiData = wikiRes.data;

        // If Wikipedia redirects or summary is wrong → fallback to search API
        if (
            !wikiData.title ||
            wikiData.title.toLowerCase() !== placeName.toLowerCase() ||
            wikiData.type === "disambiguation" ||
            wikiData.title === "Not found."
        ) {
            console.log("⚠️ Wikipedia summary mismatch, using search fallback for:", placeName);

            const searchRes = await axios.get(
                `https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(
                    placeName
                )}&format=json`,
                {
                    headers: {
                        "Cache-Control": "no-cache",
                        Pragma: "no-cache",
                        Expires: "0",
                    },
                }
            );

            const bestMatch = searchRes.data?.query?.search?.[0];
            if (bestMatch) {
                const bestTitle = bestMatch.title;

                // Fetch summary for best match
                const summaryRes = await axios.get(
                    `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(bestTitle)}`,
                    {
                        headers: {
                            "Cache-Control": "no-cache",
                            Pragma: "no-cache",
                            Expires: "0",
                        },
                    }
                );

                wikiData = summaryRes.data;
            }
        }

        // Cleanup uploaded file
        fs.unlink(filePath, (err) => {
            if (err) console.error("⚠️ Error deleting file:", err.message);
            else console.log("🗑️ Deleted file:", filePath);
        });

        // Final response
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
        console.error("❌ Monument detection error:", error.response?.data || error.message);

        if (filePath) {
            fs.unlink(filePath, (err) => {
                if (err) console.error("⚠️ Error deleting file after failure:", err.message);
            });
        }

        res.status(500).json({
            error: "Something went wrong",
            details: error.response?.data || error.message,
        });
    }
};
