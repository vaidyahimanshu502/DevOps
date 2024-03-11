import { getCommonContainer, getCommonTitle } from "egov-ui-framework/ui-config/screens/specs/utils";

const declarationDetails = getCommonContainer({
  citizenApproval: {
    uiFramework: "custom-containers-local",
    moduleName: "egov-bpa",
    componentPath: "BpaCheckboxContainer",
    jsonPath: "BPA.isDeclared",
    props: {
      label: {
        labelName: "I hereby Solemnly affirm and declare that the information as furnished is true and correct to the best of my knowledge and belief. I further undertake that if any information at any stage shall be found to be false, my application registration shall be liable to be canceled without any prior notice in that regard and I shall not claim any compensation etc. for such default on my part. In case of any discrepancies found later, I shall be liable for punishment under the relevant provisions of Law as also under Municipal Act and the Act.  I hereby authorize the technical empanelled person to Submit a building plan application on my behalf.",
        labelKey: ["BPA_CITIZEN_1_DECLARAION_LABEL", "BPA_CITIZEN_2_DECLARAION_LABEL"] //"BPA_CITIZEN_DECLARAION_LABEL"
      },
      jsonPath: "BPA.isDeclared",
    },
    visible: false,
    type: "array"
  }
});

export const declarations = getCommonContainer({
  headers: getCommonTitle(
    {
      labelName: "Declaration",
      labelKey: "BPA_DECLARATION_TITLE"
    },
    {
      style: {
        marginBottom: 10,
        marginTop: 18
      }
    }
  ),
  header: {
    uiFramework: "custom-atoms",
    componentPath: "Container",
    props: {
      style: {
        margin: "10px"
      }
    },
    children: {
      body: declarationDetails
    }
  }
});
