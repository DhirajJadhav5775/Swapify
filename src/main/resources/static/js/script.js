// Simulate data (replace this with AJAX or fetch from backend)
document.addEventListener("DOMContentLoaded", function () {
    const users = [
        { id: 1, name: "Dhiraj Jadhav", email: "dhiraj@example.com", date: "2025-03-10" },
        { id: 2, name: "Sneha Patil", email: "sneha@example.com", date: "2025-04-01" }
    ];

    const vouchers = [
        { id: 101, title: "Flipkart ₹500 Off", seller: "Dhiraj Jadhav", price: "₹400", status: "Available" },
        { id: 102, title: "Amazon ₹1000 Off", seller: "Sneha Patil", price: "₹850", status: "Sold" }
    ];

    // Populate users
    const userTable = document.querySelector("#usersTable tbody");
    if (userTable) {
        users.forEach(user => {
            const row = `<tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.date}</td>
            </tr>`;
            userTable.innerHTML += row;
        });
    }

    // Populate vouchers
    const voucherTable = document.querySelector("#voucherTable tbody");
    if (voucherTable) {
        vouchers.forEach(v => {
            const row = `<tr>
                <td>${v.id}</td>
                <td>${v.title}</td>
                <td>${v.seller}</td>
                <td>${v.price}</td>
                <td>${v.status}</td>
            </tr>`;
            voucherTable.innerHTML += row;
        });
    }
});
