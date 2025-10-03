import { fetchFestivalsByYear } from "../config/calendarific.js";


export const getFestivalsByYear = async (req, res) => {
  try {
    const { year } = req.params;
    console.log("📅 Fetching festivals for year:", year);

    const festivals = await fetchFestivalsByYear(year);

    console.log("✅ Festivals fetched:", festivals.length);
    res.json(festivals);
  } catch (error) {
    console.error("❌ Error in getFestivalsByYear:", error.message);
    res.status(500).json({ error: "Failed to fetch festivals by year" });
  }
};


export const getFestivalsByMonth = async (req, res) => {
  try {
    const { year, month } = req.params;
    console.log(`📅 Fetching festivals for ${year}-${month}`);

    const festivals = await fetchFestivalsByYear(year);

    const monthlyFestivals = festivals.filter(f => {
      const festDate = new Date(f.date.iso);
      return festDate.getMonth() + 1 === parseInt(month);
    });

    console.log("✅ Festivals for month:", monthlyFestivals.length);
    res.json(monthlyFestivals);
  } catch (error) {
    console.error("❌ Error in getFestivalsByMonth:", error.message);
    res.status(500).json({ error: "Failed to fetch festivals by month" });
  }
};

export const getUpcomingFestivals = async (req, res) => {
  try {
    const today = new Date();
    const year = today.getFullYear();

    console.log("🔮 Fetching upcoming festivals from:", today.toISOString());

    const festivals = await fetchFestivalsByYear(year);
    const nextYearFestivals = await fetchFestivalsByYear(year + 1);

    const allFestivals = [...festivals, ...nextYearFestivals];

    const upcoming = allFestivals.filter(f => {
      const festDate = new Date(f.date.iso);
      return festDate >= today;
    });

    upcoming.sort((a, b) => new Date(a.date.iso) - new Date(b.date.iso));

    console.log("✅ Upcoming festivals count:", upcoming.length);
    res.json(upcoming);
  } catch (error) {
    console.error("❌ Error in getUpcomingFestivals:", error.message);
    res.status(500).json({ error: "Failed to fetch upcoming festivals" });
  }
};
