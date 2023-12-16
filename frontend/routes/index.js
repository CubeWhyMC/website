var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', {});
});

router.get('/farewell', function(req, res, next) {
  res.render('farewell', {});
});

router.get('/download/:v', function(req, res, next) {
  res.redirect("https://github.com/CubeWhyMC/LunarClient-CN/releases/" + req.params.v)
});

router.get("/thanks", function(req, res) {
  res.render("thanks"); // process link in the frontend
})

router.get("/download", (req, res) => {
  res.render('download', {
    api: process.env.backend
  });
})

router.get('/help', function(req, res, next) {
  res.redirect("https://github.com/CubeWhyMC/LunarClient-CN/wiki");
});

router.get('/en', function(req, res, next) {
  res.render('index-en', {});
});

router.get('/en/download', function(req, res, next) {
  res.render('download-en', {
    api: process.env.backend
  });
});

router.get("/donate", function(req, res) {
  res.render("donate", {})
});

router.get("/celestial", function(req, res) {
  res.render("celestial/index")
})

router.get("/new", function(req, res) {
  res.redirect("/celestial")
})

module.exports = router;
