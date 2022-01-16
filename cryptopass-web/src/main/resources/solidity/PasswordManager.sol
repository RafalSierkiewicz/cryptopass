// SPDX-License-Identifier: MIT
pragma solidity >=0.8.4 <0.9.0;

contract PasswordManager {
    struct User {
        address _address;
        bytes32 masterPassword;
        bool isAllowed;
    }

    struct PasswordEntity {
        string title;
        string username;
        string password;
    }

    address private owner;
    mapping(address => User) private users;
    mapping(address => mapping(string => PasswordEntity)) private passwords;
    mapping(address => string[]) private titles;

    constructor(address _owner) {
        owner = _owner;
    }

    function store(
        string calldata _masterPassword,
        string calldata title,
        string calldata username
    ) public onlyUser(_masterPassword) {
        require(isNonEmpty(_masterPassword));
        require(isNonEmpty(title));
        require(isNonEmpty(username));
        require(isEmpty(passwords[msg.sender][title].username));
        PasswordEntity memory password = passwords[msg.sender][title];
        password.title = title;
        password.username = username;
        password.password = generatePassword(_masterPassword);

        passwords[msg.sender][title] = password;
    }

    function getAll(string calldata _masterPassword)
        public
        view
        returns (PasswordEntity[] memory)
    {
        require(isNonEmpty(_masterPassword));
        string[] memory uTitles = titles[msg.sender];
        PasswordEntity[] memory all = new PasswordEntity[](0);

        for (uint256 i; i < uTitles.length; i++) {
            all[i] = passwords[msg.sender][uTitles[i]];
        }
        return all;
    }

    modifier onlyOwner() {
        require(msg.sender == owner);
        _;
    }

    modifier onlyUser(string calldata _masterPassword) {
        require(
            users[msg.sender].isAllowed == true &&
                keccak256(bytes(_masterPassword)) ==
                users[msg.sender].masterPassword
        );
        _;
    }

    function encode(string calldata _key, string memory str)
        private
        pure
        returns (string memory)
    {
        return str;
    }

    function generatePassword(string calldata key)
        private
        pure
        returns (string memory)
    {
        return encode(key, string(abi.encode(string("generatedPass"), key)));
    }

    function isNonEmpty(string memory str) private pure returns (bool) {
        return bytes(str).length > 0;
    }

    function isEmpty(string memory str) private pure returns (bool) {
        return !isNonEmpty(str);
    }
}
