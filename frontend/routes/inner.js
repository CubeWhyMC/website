let express = require('express');
let router = express.Router();

router.get("/publish-mod", (req, res) => {
    res.render("inner/publish-mod", {})
})

module.exports = router;