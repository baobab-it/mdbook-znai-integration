puppeteer=require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();

  await page.goto('http://localhost:8000/docs/');
  
  const textContent = await page.evaluate(() => {
    return document.querySelector('.section').innerHTML;
  });

  console.log(textContent);
  
  await browser.close();
})();
