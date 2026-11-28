// Map + address search for the Suggest a Spot form

const latInput = document.getElementById('latitude');
const lonInput = document.getElementById('longitude');
const addressInput = document.getElementById('address');
const findButton = document.getElementById('findAddress');
const statusText = document.getElementById('geocodeStatus');

// start zoomed out over Metro Vancouver
const suggestMap = L.map('suggestMap').setView([49.26, -123.05], 11);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(suggestMap);

let pin = null;

// put the pin at lat/lon and copy its position into the hidden inputs
function placePin(lat, lon) {
    if (!pin) {
        pin = L.marker([lat, lon], { draggable: true }).addTo(suggestMap);
        pin.on('dragend', function () {
            const pos = pin.getLatLng();
            saveCoordinates(pos.lat, pos.lng);
        });
    } else {
        pin.setLatLng([lat, lon]);
    }
    saveCoordinates(lat, lon);
}

function saveCoordinates(lat, lon) {
    latInput.value = lat.toFixed(6);
    lonInput.value = lon.toFixed(6);
}

function showStatus(message, isError) {
    statusText.textContent = message;
    statusText.className = isError ? 'error' : 'hint';
}

// clicking the map also places the pin
suggestMap.on('click', function (e) {
    placePin(e.latlng.lat, e.latlng.lng);
});

// if the form came back with errors, put the pin back where it was
if (latInput.value && lonInput.value) {
    const lat = parseFloat(latInput.value);
    const lon = parseFloat(lonInput.value);
    placePin(lat, lon);
    suggestMap.setView([lat, lon], 17);
}

async function findAddress() {
    const address = addressInput.value.trim();
    if (!address) {
        showStatus('Type an address first.', true);
        return;
    }

    findButton.disabled = true;
    showStatus('Searching...', false);

    try {
        const res = await fetch('/api/geocode?address=' + encodeURIComponent(address));
        const data = await res.json();

        if (!res.ok) {
            showStatus(data.error || 'Something went wrong. Place the pin manually.', true);
            return;
        }

        placePin(data.lat, data.lon);
        suggestMap.setView([data.lat, data.lon], 17);
        showStatus('Found: ' + data.displayName + '. Drag the pin if it is not exactly right.', false);
    } catch (error) {
        console.error('Address search failed:', error);
        showStatus('Address search failed. Place the pin manually by clicking the map.', true);
    } finally {
        findButton.disabled = false;
    }
}

findButton.addEventListener('click', findAddress);

// pressing Enter in the address box searches instead of submitting the whole form
addressInput.addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        findAddress();
    }
});