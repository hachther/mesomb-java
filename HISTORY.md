# 2.0.0 (2025-03-05)

## Added

- Add fundraising operations
- Add wallet operations
- Add refund transaction operation

## === BREAKING CHANGES ===

- Parameters for make_collect and make_deposit are not more passed as dict but as keyword arguments
- Remove security operations
- Change parameter ts(str) to date(datetime) in Transaction class

# 1.1.2 (2023-07-26)

Fix bug of request body stringify

# 1.1.1 (2023-07-03)

- Fixing some bug of status in Transaction response

# 1.1.0 (2023-06-23)

## === BREAKING CHANGES ===
Only one parameter is now passed to makeDeposit and makeCollect. The parameter is a Map that will contain all details of your request.

# 1.0.3 (2023-05-06)

Update gradle config and make the code compatible with JAVA 1.8

# 1.0.2 (2023-05-06)

Update gradle config and make the code compatible with JAVA 1.8

# 1.0.1 (2023-03-24)

- Reorganize some parts of the code
- Implementation of checkTransaction of the endpoint to check the status of a transaction at the operator level
