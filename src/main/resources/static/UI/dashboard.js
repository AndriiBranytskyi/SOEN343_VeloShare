import { authFetch } from "./auth.js";

//get req to backend
export async function loadStation(name) {
  const res = await fetch(`/api/stations/${encodeURIComponent(name)}`);
  const data = await res.json();
  return data;
}

export async function stationSummaryText(data) {
  return (
    `Address: ${data.address || "N/A"}\n` +
    `Standard bikes: ${data.standardBikes ?? 0}\n` +
    `E-bikes: ${data.eBikes ?? 0}`
  );
}

export async function reserve(userId, bikeId, stationName, minutes) {
  const res = await authFetch(`/api/reservations`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userId, bikeId, stationName, minutes }),
  });
  if (!res.ok) throw new Error(await res.text());
  return await res.text();
}

export async function startTrip({ bikeId, stationName }) {
  const res = await authFetch(`/api/trips/start`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      bikeId,
      stationName,
      estimatedCost: 0,
      estimatedDistance: 0,
    }),
  });
  if (!res.ok) throw new Error(await res.text());
  const { tripId } = await res.json();
  return tripId;
}

export async function endTrip({ tripId, stationName }) {
  const res = await authFetch(`/api/trips/end`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ tripId, stationName }),
  });
  if (!res.ok) throw new Error(await res.text());
  return await res.json(); // <- bill: { tripId, userId, minutesBilled, amountCents }
}

export async function cancelReservation(reservationId) {
  const res = await authFetch(
    `/api/reservations/${encodeURIComponent(reservationId)}`,
    {
      method: "DELETE",
    }
  );
  if (!res.ok && res.status !== 204) {
    throw new Error(await res.text());
  }
}

export async function moveBike({ bikeId, fromStation, toStation }) {
  const res = await authFetch(`/api/ops/move-bike`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      bikeId,
      fromStation,
      toStation,
    }),
  });
  if (!res.ok) throw new Error(await res.text());
}

export async function setStationOOS(stationName) {
  const res = await authFetch(
    `/api/ops/stations/${encodeURIComponent(stationName)}/oos`,
    {
      method: "POST",
    }
  );
  if (!res.ok) throw new Error(await res.text());
}

export async function setStationActive(stationName) {
  const res = await authFetch(
    `/api/ops/stations/${encodeURIComponent(stationName)}/active`,
    {
      method: "POST",
    }
  );
  if (!res.ok) throw new Error(await res.text());
}

export async function setBikeMaintenance(bikeId) {
  const res = await authFetch(
    `/api/ops/bikes/${encodeURIComponent(bikeId)}/maintenance`,
    {
      method: "POST",
    }
  );
  if (!res.ok) throw new Error(await res.text());
}
export async function setBikeAvailable(bikeId) {
  const res = await authFetch(
    `/api/ops/bikes/${encodeURIComponent(bikeId)}/available`,
    {
      method: "POST",
    }
  );
  if (!res.ok) throw new Error(await res.text());
}

export async function getProfile() {
  const r = await authFetch("/api/profile");
  if (!r.ok) throw new Error(await r.text());
  return await r.json(); // { uid, name, role, canOperate, canRide}
}

export async function fetchMyBills() {
  const res = await authFetch("/api/billing/mine");
  if (!res.ok) throw new Error(await res.text());
  return await res.json();
}
