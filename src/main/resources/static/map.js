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


//filter function and load edits
function filterAndRenderMarkers() {
    // Clear current markers from the map layer group
    markersLayer.clearLayers();

    // 1. Check if the Cafe toggle is checked
    const cafesToggle = document.getElementById('cafeToggle');
    if (cafesToggle && cafesToggle.checked && cafeGeojsonData) {
        addGeoJSONToLayer(cafeGeojsonData);
    }

    const sfuToggle = document.getElementById('SFUToggle');
    if (sfuToggle && sfuToggle.checked && sfuGeojsonData) {
        // Gather checked sub-categories
        const sfuCheckboxes = document.querySelectorAll('#filterForm input[name="category"]:checked');
        const checkedTypes = Array.from(sfuCheckboxes).map(cb => cb.value.toLowerCase());

        // Only render if at least one sub-category is checked
        if (checkedTypes.length > 0) {
            addGeoJSONToLayer(sfuGeojsonData, function(feature) {
                const props = feature.properties || {};
                const featureType = (props.type || '').toLowerCase();
                
                // Return true if the feature matches ANY of the checked SFU categories
                return checkedTypes.some(type => featureType.includes(type));
            });
        }
    }
    
}

//event checks to see if the map needs update
const cafeToggle = document.getElementById('cafeToggle');
if (cafeToggle) {
    cafeToggle.addEventListener('change', filterAndRenderMarkers);
}

const sfuToggle = document.getElementById('SFUToggle');
if (sfuToggle) {
    sfuToggle.addEventListener('change', filterAndRenderMarkers);
}

// Listen for changes on all SFU category checkboxes
document.querySelectorAll('#filterForm input[name="category"]').forEach(cb => {
    cb.addEventListener('change', filterAndRenderMarkers);
});
