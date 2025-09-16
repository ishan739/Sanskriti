// tools/eventApi.js
import axios from 'axios';

export const findEvents = async ({ category, location }) => {
  try {
    // Replace with a real API endpoint
    const apiResponse = await axios.get('https://example-event-api.com/events', {
      params: { category, location },
    });

    const events = apiResponse.data.events.map(event => ({
      eventName: event.title,
      category: event.category,
      date: event.start_date,
      time: event.start_time,
      location_organizer: event.venue,
    }));

    return { upcoming_events: events };
  } catch (error) {
    // Catching the error here prevents an unhandled rejection
    console.error('Error in findEvents:', error.message);
    return {
      error: 'Failed to retrieve events from the external API.',
      details: error.message,
    };
  }
};