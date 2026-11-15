
const API_URL = "/api/registrations";

const registrationForm = document.getElementById("registrationForm");
const messageBox = document.getElementById("message");

// Load registration information from Java backend
async function loadRegistrations() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Unable to load registration information.");
        }

        const data = await response.json();

        // Update dashboard statistics
        document.getElementById("capacity").textContent = data.capacity;
        document.getElementById("enrolledCount").textContent =
            data.enrolledLearners.length;
        document.getElementById("availableSeats").textContent =
            data.availableSeats;
        document.getElementById("waitlistCount").textContent =
            data.waitlistedLearners.length;

        displayEnrolledLearners(data.enrolledLearners);
        displayWaitlistedLearners(data.waitlistedLearners);

    } catch (error) {
        showMessage(error.message, "error");
    }
}

// Display enrolled learners
function displayEnrolledLearners(learners) {
    const table = document.getElementById("enrolledTable");

    if (learners.length === 0) {
        table.innerHTML =
            '<tr><td colspan="3">No enrolled learners.</td></tr>';
        return;
    }

    table.innerHTML = learners.map(learner => `
        <tr>
            <td>${escapeHtml(learner)}</td>
            <td><span class="status enrolled">Enrolled</span></td>
            <td>
                <button class="drop-button"
                    data-learner-id="${encodeURIComponent(learner)}">
                    Drop
                </button>
            </td>
        </tr>
    `).join("");
}

// Display waitlisted learners in FIFO order
function displayWaitlistedLearners(learners) {
    const table = document.getElementById("waitlistTable");

    if (learners.length === 0) {
        table.innerHTML =
            '<tr><td colspan="3">No learners on the waitlist.</td></tr>';
        return;
    }

    table.innerHTML = learners.map((learner, index) => `
        <tr>
            <td>${index + 1}</td>
            <td>${escapeHtml(learner)}</td>
            <td><span class="status waitlisted">Waitlisted</span></td>
        </tr>
    `).join("");
}

// Register a learner
registrationForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const learnerId = document.getElementById("learnerId").value.trim();
    const prerequisiteSatisfied =
        document.getElementById("prerequisite").value === "true";

    if (!learnerId) {
        showMessage("Please enter a learner ID.", "error");
        return;
    }

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                learnerId: learnerId,
                prerequisiteSatisfied: prerequisiteSatisfied
            })
        });

        const result = await readResponse(response);

        showMessage(
            `${result.learnerId}: ${result.message}`,
            response.ok ? "success" : "error"
        );

        if (response.ok) {
            registrationForm.reset();
            await loadRegistrations();
        }

    } catch (error) {
        showMessage(error.message, "error");
    }
});

// Drop a learner using event delegation
document.getElementById("enrolledTable")
    .addEventListener("click", async function(event) {

        const button = event.target.closest(".drop-button");

        if (!button) {
            return;
        }

        const learnerId = decodeURIComponent(
            button.dataset.learnerId
        );

        if (!confirm(`Are you sure you want to drop ${learnerId}?`)) {
            return;
        }

        try {
            const response = await fetch(
                `${API_URL}/${encodeURIComponent(learnerId)}`,
                { method: "DELETE" }
            );

            const result = await readResponse(response);

            let message = result.message;

            if (result.promotedLearnerId) {
                message += ` Promoted learner: ${result.promotedLearnerId}`;
            }

            showMessage(message, response.ok ? "success" : "error");

            if (response.ok) {
                await loadRegistrations();
            }

        } catch (error) {
            showMessage(error.message, "error");
        }
    });

// Read JSON or plain-text API responses
async function readResponse(response) {
    const text = await response.text();

    let result;

    try {
        result = JSON.parse(text);
    } catch {
        result = { message: text };
    }

    if (!response.ok) {
        throw new Error(result.message || "Request failed.");
    }

    return result;
}

// Prevent learner IDs from being interpreted as HTML
function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}

// Display feedback messages
function showMessage(message, type) {
    messageBox.textContent = message;
    messageBox.className = `message ${type}`;
}

// Load dashboard when page opens
loadRegistrations();
