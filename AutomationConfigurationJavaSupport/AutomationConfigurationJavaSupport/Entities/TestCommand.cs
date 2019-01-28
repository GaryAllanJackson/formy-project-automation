using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AutomationConfigurationJavaSupport.Entities
{
    public class TestCommand
    {
        //[DisplayName("Accessor")]
        public string Accessor { get; set; }

        //[DisplayName("Expected Value/Action")]
        public string ExpectedValueAction { get; set; }

        //[DisplayName("Accessor Type")]
        public string AccessorType { get; set; }

        //[DisplayName("Perform Non-Read Action")]
        public string IsNonReadAction { get; set; }

        public string IsCrucial { get; set; }

    }
}
