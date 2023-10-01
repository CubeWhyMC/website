let express = require('express');
let router = express.Router();

router.get("/", function (req, res) {
    res.render("liquid/index.ejs")
})

router.get("/about", function (req, res) {
    res.render("liquid/about.ejs")
})

module.exports = router;
