
import axios from "axios";

function cleanDescription(text) {
  if (!text) return "No description available.";
  return text.replace(/\.\.\.$/, "").trim().slice(0, 300);
}


function normalizeDate(dateStr) {
  if (!dateStr || dateStr === "-") return "N/A";

  const parsed = Date.parse(dateStr);
  if (!isNaN(parsed)) {
    return new Date(parsed).toISOString().split("T")[0];
  }
  return dateStr;
}


async function fetchWikiImage(query) {
  try {
    const url = `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(query)}`;
    const { data } = await axios.get(url);
    return data.thumbnail?.source || null;
  } catch {
    return null;
  }
}


export async function getEvents(req, res) {
  try {
    const { city } = req.body;
    if (!city) {
      return res.status(400).json({ error: "City is required" });
    }

    const fastApiUrl = "https://event-ugfy.onrender.com/get-events";
    const { data: events } = await axios.post(fastApiUrl, { city });

    const cityImage = await fetchWikiImage(city);

    
    const enhanced = await Promise.all(
      events.map(async (ev) => {
        let imageUrl = await fetchWikiImage(ev.name);

        if (!imageUrl) {
          imageUrl = cityImage;
        }

        return {
          date: normalizeDate(ev.date),
          name: ev.name || "Unnamed Event",
          place: ev.place || city,
          booking_link: ev.booking_link || "N/A",
          description: cleanDescription(ev.description),
          image_url: imageUrl,
        };
      })
    );

    res.json(enhanced);
  } catch (error) {
    console.error("Error fetching events:", error.message);
    res.status(500).json({ error: "Something went wrong" });
  }
}
