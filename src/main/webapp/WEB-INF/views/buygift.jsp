<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Available Vouchers</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h2 class="text-center mb-4">Available Vouchers</h2>
    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>brand</th>
                <th>details (₹)</th>
                <th>promocode</th>
                <th>price</th>
                <th>expdate</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="gift" items="${gifts}">
            <tr>
                <td>${gift.id}</td>
                <td>${gift.brand}</td>
                <td>${gift.details}</td>
                <td>${gift.promocode}</td>
                <td>${gift.price}</td>
                <td>${gift.expdate}</td>
                <td>
                    <form method="post" action="/buygift">
                        <input type="hidden" name="id" value="${gift.id}">
                        <button type="submit" class="btn btn-success btn-sm">Buy</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
