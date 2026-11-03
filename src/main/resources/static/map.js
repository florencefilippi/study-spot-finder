// 1. Initialize map centered on Vancouver
const initialLat = 49.28273;
const initialLng = -123.12074;
const map = L.map('map').setView([initialLat, initialLng], 15);

const cafeGeoJSONPath = 'data/vancouver-cafes.geojson'; 
const sfuGeoJSONPath = 'data/sfu-studySpots.geojson'; 

// Initialize a Layer Group for markers
const markersLayer = L.layerGroup().addTo(map);

// 2. Add OpenStreetMap tile layer
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

//Global variables
let cafeGeojsonData = null;
let sfuGeojsonData = null;


async function loadAllData() {
    try {
        // Fetch both files concurrently
        const [cafeRes, sfuRes] = await Promise.all([
            fetch(cafeGeoJSONPath),
            fetch(sfuGeoJSONPath)
        ]);

        if (!cafeRes.ok || !sfuRes.ok) {
            throw new Error("Failed to load one or more GeoJSON files.");
        }

        cafeGeojsonData = await cafeRes.json();
        sfuGeojsonData = await sfuRes.json();

        // Render markers initially based on default checked boxes
        filterAndRenderMarkers();

    } catch (error) {  
        console.error("Error loading GeoJSON files:", error);
    }
}
function addGeoJSONToLayer(geojsonData, filterFunction = null) {
    L.geoJSON(geojsonData, {
        pointToLayer: function (feature, latlng) {
            return L.marker(latlng);
        },
        filter: filterFunction,
        onEachFeature: onEachFeature
    }).addTo(markersLayer);
}

function onEachFeature(feature, layer) {
    const props = feature.properties || {};
    const name = props.name || 'Unnamed Location';

    let lat, lon;
    if (feature.geometry.type === 'Point') {
        lon = feature.geometry.coordinates[0];
        lat = feature.geometry.coordinates[1];
    } else {
        const center = layer.getBounds().getCenter();
        lat = center.lat;
        lon = center.lng;
    }

    const googleMapsUrl = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(name)}+(${lat},${lon})`;

    layer.bindPopup(`
        <div style="font-family: sans-serif;">
            <h3 style="margin: 0 0 5px 0; font-size: 16px;">${name}</h3>
            ${props.type ? `<p style="margin: 0 0 3px 0; font-size: 12px;"><b>Type:</b> ${props.type}</p>` : ''}
            ${props.opening_hours ? `<p style="margin: 0 0 5px 0; font-size: 12px;"><b>Hours:</b> ${props.opening_hours}</p>` : ''}
            <a href="${googleMapsUrl}" target="_blank" rel="noopener noreferrer" style="color: #0078A8;">
                Open in Google Maps ↗
            </a>
        </div>
    `);
}

loadAllData();
