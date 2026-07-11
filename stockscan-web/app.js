const API_BASE = new URLSearchParams(location.search).get("api") || "http://localhost:8080";

const statusEl = document.getElementById("status");
const itemsBody = document.querySelector("#items tbody");
const movementsBody = document.querySelector("#movements tbody");

async function fetchJson(path) {
    const res = await fetch(`${API_BASE}${path}`);
    if (!res.ok) {
        throw new Error(`${res.status} ${res.statusText}`);
    }
    return res.json();
}

function formatTime(iso) {
    return new Date(iso).toLocaleString("ko-KR", {
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
    });
}

function renderItems(items) {
    if (items.length === 0) {
        itemsBody.innerHTML = `<tr class="empty"><td colspan="4">등록된 품목이 없습니다</td></tr>`;
        return;
    }
    itemsBody.innerHTML = items
        .map(
            (item) => `
            <tr>
                <td>${escapeHtml(item.name)}</td>
                <td>${escapeHtml(item.barcode)}</td>
                <td class="num">${item.quantity}</td>
                <td>${formatTime(item.updatedAt)}</td>
            </tr>`,
        )
        .join("");
}

function renderMovements(movements) {
    if (movements.length === 0) {
        movementsBody.innerHTML = `<tr class="empty"><td colspan="6">이력이 없습니다</td></tr>`;
        return;
    }
    movementsBody.innerHTML = movements
        .map((movement) => {
            const inbound = movement.type === "INBOUND";
            const tag = inbound
                ? `<span class="tag inbound">입고</span>`
                : `<span class="tag outbound">출고</span>`;
            const signed = `${inbound ? "+" : "−"}${movement.quantity}`;
            return `
            <tr>
                <td>${formatTime(movement.createdAt)}</td>
                <td>${escapeHtml(movement.barcode)}</td>
                <td>${tag}</td>
                <td class="num">${signed}</td>
                <td class="num">${movement.quantityAfter}</td>
                <td>${escapeHtml(movement.memo ?? "")}</td>
            </tr>`;
        })
        .join("");
}

function escapeHtml(value) {
    return String(value).replace(/[&<>"]/g, (ch) => {
        return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[ch];
    });
}

async function load() {
    statusEl.textContent = "불러오는 중…";
    statusEl.classList.remove("error");
    try {
        const [items, movements] = await Promise.all([
            fetchJson("/api/items"),
            fetchJson("/api/movements"),
        ]);
        renderItems(items);
        renderMovements(movements);
        statusEl.textContent = `업데이트 ${new Date().toLocaleTimeString("ko-KR")}`;
    } catch (err) {
        statusEl.textContent = `서버 연결 실패 (${err.message})`;
        statusEl.classList.add("error");
    }
}

document.getElementById("refresh").addEventListener("click", load);
load();
