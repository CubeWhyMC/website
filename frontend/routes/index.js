var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', {});
});

router.get('/download', function(req, res, next) {
  res.render('download', {});
});

router.get('/help', function(req, res, next) {
  res.redirect("https://github.com/CubeWhyMC/LunarClient-CN/wiki");
});

router.get('/en', function(req, res, next) {
  res.render('index-en', {});
});

router.get('/en/download', function(req, res, next) {
  res.render('download-en', {});
});

router.get("/donate", function(req, res) {
  res.render("donate", {})
});

module.exports = router;
