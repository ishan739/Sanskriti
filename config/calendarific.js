import axios from "axios";

const API_KEY = process.env.CALENDARIFIC_API_KEY; 
const BASE_URL = "https://calendarific.com/api/v2";

export const fetchFestivalsByYear = async (year) => {
  try {
    console.log("🔑 API KEY:", API_KEY); // debug check
    const response = await axios.get(`${BASE_URL}/holidays`, {
      params: {
        api_key: API_KEY,
        country: "IN",
        year: year
      }
    });
    return response.data.response.holidays;
  } catch (error) {
    console.error("Calendarific API Error:", error.response?.data || error.message);
    throw error;
  }
};
