using System;
using System.IO;
using System.Net;
using System.Text;

namespace AutomationConfigurationJavaSupport.Helpers
{
    public class WebRetrievalHelper
    {
        /// <summary>
        /// Error Message string property that can be checked by a calling class in case an error occurs
        /// </summary>
        public string ErrorMessage { get; set; }

        /// <summary>
        /// Retrieves the Source for the Page at the urlAddress Passed in.
        /// </summary>
        /// <param name="urlAddress"></param>
        /// <returns>Page source as string</returns>
        public string GetWebSourceWithHeaderSimple(string urlAddress, string clientId = null, string clientSecret = null)
        {
            try
            {
                //FileHelper fileHelper = new FileHelper();
                //creates an HttpWebRequest object for the URL address passed in
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(urlAddress);
                //sets this as a get request
                request.Method = WebRequestMethods.Http.Get;
                //check to see if authorization header creds exist and if so, creates an Authorization header
                //if (!string.IsNullOrEmpty(AppConstants.ClientId))
                if (!string.IsNullOrEmpty(clientId))
                {
                    //string authInfo = AppConstants.ClientId + ":" + AppConstants.ClientSecret;
                    string authInfo = clientId + ":" + clientSecret;
                    authInfo = Convert.ToBase64String(Encoding.Default.GetBytes(authInfo));
                    request.Headers["Authorization"] = "Basic " + authInfo;
                }
                if (urlAddress.IndexOf("https://") > 0)
                {
                    ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls
                                     | SecurityProtocolType.Tls11
                                     | SecurityProtocolType.Tls12;
                    //request.ServicePoint = new ServicePoint()
                }
                //execute the request and get a response
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();

                //check the response and if ok, read the stream into a string variable and return that string.
                if (response.StatusCode == HttpStatusCode.OK)
                {
                    Stream receiveStream = response.GetResponseStream();
                    StreamReader readStream = null;

                    if (string.IsNullOrEmpty(response.CharacterSet))
                    {
                        readStream = new StreamReader(receiveStream);
                    }
                    else
                    {
                        readStream = new StreamReader(receiveStream, Encoding.GetEncoding(response.CharacterSet));
                    }

                    string data = readStream.ReadToEnd();

                    response.Close();
                    readStream.Close();

                    return data;
                }
            }
            catch (ProtocolViolationException px)
            {
                //catch protocol violations
                ErrorMessage = px.Message;
                return ErrorMessage;
            }
            catch (WebException wx)
            {
                //catch Web exceptions
                ErrorMessage = wx.Message;
                return ErrorMessage;
            }
            catch (Exception ex)
            {
                //catch any exceptions differing from those above
                ErrorMessage = ex.Message;
                return ErrorMessage;
            }
            return null;
        }


    }
}
